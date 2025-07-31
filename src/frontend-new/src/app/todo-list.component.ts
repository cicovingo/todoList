import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Todo } from './todo';
import { TodoService } from './todo.service';

import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { CardModule } from 'primeng/card';
import { RippleModule } from 'primeng/ripple';

@Component({
  selector: 'app-todo-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    InputTextModule,
    ButtonModule,
    CheckboxModule,
    DialogModule,
    ConfirmDialogModule,
    ToastModule,
    CardModule,
    RippleModule,
  ],
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.scss'],
  providers: [ConfirmationService, MessageService]
})
export class TodoListComponent implements OnInit {
  todos: Todo[] = [];
  selectedTodo: Todo | null = null;
  newTodo: Todo = Todo.createNew();
  editingTodo: Todo | null = null;
  displayEditDialog: boolean = false;

  constructor(
    private todoService: TodoService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.getTodos();
  }

  getTodos(): void {
    this.todoService.getTodos()
      .then((todos: Todo[]) => {
        this.todos = todos;
      })
      .catch((error: any) => {
        console.error('Error fetching todos:', error);
        this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Todolar yüklenemedi.' });
      });
  }

  addTodo(): void {
    if (!this.newTodo.title || this.newTodo.title.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: 'Uyarı', detail: 'Todo başlığı boş olamaz!' });
      return;
    }

    this.todoService.createTodo(this.newTodo)
      .then((createdTodo: Todo) => {
        this.todos.push(createdTodo);
        this.newTodo = Todo.createNew();
        this.messageService.add({ severity: 'success', summary: 'Başarılı', detail: 'Todo başarıyla eklendi.' });
      })
      .catch((error: any) => {
        console.error('Error creating todo:', error);
        this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Todo eklenirken bir sorun oluştu.' });
      });
  }

  editTodo(todo: Todo): void {
    this.editingTodo = { ...todo };
    this.displayEditDialog = true;
  }

  updateTodo(): void {
    if (this.editingTodo && this.editingTodo.title && this.editingTodo.title.trim() !== '') {
      this.todoService.updateTodo(this.editingTodo)
        .then((updatedTodo: Todo) => {
          const index = this.todos.findIndex(t => t.id === updatedTodo.id);
          if (index !== -1) {
            this.todos[index] = updatedTodo;
          }
          this.editingTodo = null;
          this.displayEditDialog = false;
          this.messageService.add({ severity: 'success', summary: 'Başarılı', detail: 'Todo başarıyla güncellendi.' });
        })
        .catch((error: any) => {
          console.error('Error updating todo:', error);
          this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Todo güncellenirken bir sorun oluştu.' });
        });
    } else {
      this.messageService.add({ severity: 'warn', summary: 'Uyarı', detail: 'Todo başlığı boş olamaz!' });
    }
  }

  clearEditing(): void {
    this.editingTodo = null;
    this.displayEditDialog = false;
  }

  toggleCompleted(todo: Todo): void {
    const originalCompletedStatus = todo.completed;
    todo.completed = !originalCompletedStatus;

    this.todoService.updateTodo(todo)
      .then((updatedTodo: Todo) => {
        this.messageService.add({ severity: 'success', summary: 'Başarılı', detail: 'Todo durumu güncellendi.' });
      })
      .catch((error: any) => {
        console.error('Error toggling completed status:', error);
        this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Durum güncellenemedi.' });
        todo.completed = originalCompletedStatus;
      });
  }

  confirmDelete(todoToDelete: Todo): void {
    this.confirmationService.confirm({
      message: `Bu todo öğesini silmek istediğinizden emin misiniz: "${todoToDelete.title}"?`,
      header: 'Onay',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Evet',
      rejectLabel: 'Hayır',
      accept: () => {
        this.deleteTodo(todoToDelete.id);
      },
      reject: () => {
        this.messageService.add({ severity: 'info', summary: 'İptal Edildi', detail: 'Silme işlemi iptal edildi.' });
      }
    });
  }

  deleteTodo(id: string): void {
    this.todoService.deleteTodo(id)
      .then(() => {
        this.todos = this.todos.filter(t => t.id !== id);
        this.messageService.add({ severity: 'success', summary: 'Başarılı', detail: 'Todo başarıyla silindi.' });
      })
      .catch((error: any) => {
        console.error('Error deleting todo:', error);
        this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Todo silinirken bir sorun oluştu.' });
      });
  }
}
