import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { Task, TaskPriority, TaskStatus } from '../models/task';
import { TaskService } from '../services/task-service.service';
import { UserService } from '../services/user-service.service';

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html'
})
export class TaskComponent implements OnInit {
  tasks: Task[] = [];
  loading = false;
  error: string | null = null;

  model: Task = {
    userId: 0,
    title: '',
    description: '',
    status: 'OPEN',
    priority: 'NORMAL',
    completedAt: null
  };

  statuses: TaskStatus[] = ['OPEN', 'IN_PROGRESS', 'DONE', 'CANCELLED'];
  priorities: TaskPriority[] = ['LOW', 'NORMAL', 'HIGH'];

  constructor(
    private tasksApi: TaskService,
    private users: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const u = this.users.getCurrentUser();
    if (!u) { this.router.navigate(['/login']); return; }
    this.model.userId = u.id;
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.tasksApi.getByUser(this.model.userId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: list => this.tasks = list || [],
        error: err => this.error = this.prettyError(err, 'Failed to load tasks')
      });
  }

  addTask(): void {
    if (!this.model.title.trim()) return;

    const payload: any = {
      user: { id: this.model.userId }, // pro @ManyToOne
      userId: this.model.userId,       // fallback pro primitive userId
      title: this.model.title.trim(),
      description: this.model.description.trim(),
      status: this.model.status,
      priority: this.model.priority
    };

    this.tasksApi.create(payload).subscribe({
      next: created => {
        this.tasks.unshift(created);
        this.resetForm();
        this.error = null;
      },
      error: err => this.error = this.prettyError(err, 'Server error')
    });
  }

  /** smaže task (použité pro Delete i Done) */
  private removeById(id: number, taskRef?: Task): void {
    // optimisticky skryj v UI (vrátíme zpět, když to spadne)
    const prev = [...this.tasks];
    if (taskRef) this.tasks = this.tasks.filter(t => t !== taskRef);

    this.tasksApi.remove(id).subscribe({
      next: () => { /* OK, nic dalšího netřeba */ },
      error: err => {
        this.error = this.prettyError(err, 'Delete failed');
        this.tasks = prev; // revert UI
      }
    });
  }

  /** klik na Delete */
  deleteTask(t: Task): void {
    const id = (t as any).id as number | undefined;
    if (id == null) { return; }
    this.removeById(id, t);
  }

  /** klik na Done – podle zadání task rovnou odstraníme */
  markDone(t: Task): void {
    const id = (t as any).id as number | undefined;
    if (id == null) { return; }
    this.removeById(id, t);
  }

  private resetForm(): void {
    this.model = {
      userId: this.model.userId,
      title: '',
      description: '',
      status: 'OPEN',
      priority: 'NORMAL',
      completedAt: null
    };
  }

  private prettyError(err: any, fallback: string): string {
    return err?.error?.message
        || (typeof err?.error === 'string' ? err.error : '')
        || (err?.status ? `${fallback} (HTTP ${err.status})` : fallback);
  }
}
