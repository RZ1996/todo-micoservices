import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Task } from '../models/task';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly base = environment.taskUrl; // např. http://localhost:9091

  constructor(private http: HttpClient) {}

  /** GET /tasks/user/{userId} */
  getByUser(userId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.base}/tasks/user/${userId}`);
  }

  /** POST /tasks */
  create(task: Partial<Task>): Observable<Task> {
    return this.http.post<Task>(`${this.base}/tasks`, task);
  }

  /** PATCH /tasks/{id} */
  update(id: number, patch: Partial<Task>): Observable<Task> {
    return this.http.patch<Task>(`${this.base}/tasks/${id}`, patch);
  }

  /** DELETE /tasks/{id} */
  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/tasks/${id}`);
  }
}
