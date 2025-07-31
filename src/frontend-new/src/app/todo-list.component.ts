import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Todo } from './todo';
import { TodoService } from './todo.service';

@Component({
  selector: 'app-todo-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.scss']
})
export class TodoListComponent implements OnInit {
  @ViewChild('todoForm') todoForm!: NgForm;
  todos: Todo[] = [];
  selectedTodo: Todo | null = null;
  newTodo: Todo = Todo.createNew();
  editingTodo: Todo | null = null;

  constructor(private todoService: TodoService) { }

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
      });
  }

  createTodo(form: NgForm): void {
    if (form.valid) {
      this.todoService.createTodo(this.newTodo)
        .then((createdTodo: Todo) => {
          this.todos.push(createdTodo);
          this.newTodo = Todo.createNew();
          form.resetForm(this.newTodo);
        })
        .catch((error: any) => {
          console.error('Error creating todo:', error);
        });
    } else {
      console.log('Form is invalid, cannot create todo.');
    }
  }

  editTodo(todo: Todo): void {
    this.editingTodo = { ...todo };
  }

  updateTodo(): void {
    if (this.editingTodo) {
      this.todoService.updateTodo(this.editingTodo)
        .then((updatedTodo: Todo) => {
          const index = this.todos.findIndex(t => t.id === updatedTodo.id);
          if (index !== -1) {
            this.todos[index] = updatedTodo;
          }
          this.editingTodo = null;
        })
        .catch((error: any) => {
          console.error('Error updating todo:', error);
        });
    }
  }

  toggleCompleted(todo: Todo): void {
    todo.completed = !todo.completed;
    this.todoService.updateTodo(todo)
      .then((updatedTodo: Todo) => {
      })
      .catch((error: any) => {
        console.error('Error toggling completed status:', error);
        todo.completed = !todo.completed;
      });
  }

  deleteTodo(id: string): void {
    this.todoService.deleteTodo(id)
      .then(() => {
        this.todos = this.todos.filter(todo => todo.id !== id);
        this.selectedTodo = null;
      })
      .catch((error: any) => {
        console.error('Error deleting todo:', error);
      });
  }

  clearEditing(): void {
    this.editingTodo = null;
  }
}
