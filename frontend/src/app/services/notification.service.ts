import { Injectable, computed, signal } from '@angular/core';
import { API } from '../lib/api';
import { Notification } from '../../types/notification';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  public notifications = signal<Notification[]>([]);

  unreadCount = computed(() => this.notifications().filter((n) => !n.read).length);

  async load(): Promise<void> {
    const data = await API.get<Notification[]>('/notifications');
    this.notifications.set(data);
  }

  markRead(id: string): void {
    this._patch(id, true);
    API.post(`/notifications/${id}/read`, null);
  }

  markUnread(id: string): void {
    this._patch(id, false);
    API.post(`/notifications/${id}/unread`, null);
  }

  markAllRead(): void {
    this.notifications.update((ns) => ns.map((n) => ({ ...n, read: true })));
    API.post('/notifications/read-all', null);
  }

  private _patch(id: string, read: boolean): void {
    this.notifications.update((ns) => ns.map((n) => (n.id === id ? { ...n, read } : n)));
  }
}
