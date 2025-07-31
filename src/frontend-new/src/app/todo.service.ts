import { Injectable } from '@angular/core';
import { Todo } from './todo';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { lastValueFrom, Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TodoService {
  private baseUrl = environment.apiUrl;
  private headers = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient) { }

  getTodos(): Promise<Todo[]> {
    return lastValueFrom(this.http.get<Todo[]>(this.baseUrl + '/api/todos/getTodos').pipe(
      catchError(this.handleError)
    ));
  }

  createTodo(todoData: Todo): Promise<Todo> {
    return lastValueFrom(this.http.post<Todo>(this.baseUrl + '/api/todos/createTodo', todoData, { headers: this.headers }).pipe(
      catchError(this.handleError)
    ));
  }

  updateTodo(todoData: Todo): Promise<Todo> {
    return lastValueFrom(this.http.put<Todo>(this.baseUrl + '/api/todos/updateTodo/' + todoData.id, todoData, { headers: this.headers }).pipe(
      catchError(this.handleError)
    ));
  }

  deleteTodo(id: string): Promise<any> {
    return lastValueFrom(this.http.delete(this.baseUrl + '/api/todos/deleteTodo/' + id).pipe(
      catchError(this.handleError)
    ));
  }

  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(() => new Error(error.message || 'Server error'));
  }
}
