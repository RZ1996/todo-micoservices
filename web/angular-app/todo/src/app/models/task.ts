export type TaskStatus   = 'OPEN' | 'IN_PROGRESS' | 'DONE' | 'CANCELLED';
export type TaskPriority = 'LOW'  | 'NORMAL'      | 'HIGH';

export interface Task {
  id?: number;                 // pokud backend posílá ID, necháme jako volitelné
  userId: number;
  title: string;
  description: string;
  createdAt?: string;          // ISO string (backend si stejně řeší @PrePersist/@PreUpdate)
  updatedAt?: string;
  completedAt?: string | null;
  status: TaskStatus;
  priority: TaskPriority;
}
