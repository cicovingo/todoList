export class Todo {
  id!: string;
  title!: string;
  completed!: boolean;
  createdAt!: Date;

  static createNew(): Todo {
    const newTodo = new Todo();
    newTodo.title = '';
    newTodo.completed = false;
    newTodo.createdAt = new Date();
    return newTodo;
  }

  constructor(id?: string, title?: string, completed?: boolean, createdAt?: Date) {
    if (id) this.id = id;
    if (title) this.title = title;
    if (completed !== undefined) this.completed = completed;
    if (createdAt) this.createdAt = createdAt;
    else this.createdAt = new Date();
  }
}
