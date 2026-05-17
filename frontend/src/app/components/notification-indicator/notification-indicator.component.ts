import { Component, ElementRef, HostListener, OnInit, inject, signal } from '@angular/core';
import { MatIconButton, MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDivider } from '@angular/material/divider';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { Notification } from '../../../types/notification';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'notification-indicator',
  templateUrl: './notification-indicator.html',
  styleUrl: './notification-indicator.css',
  imports: [MatIconButton, MatButton, MatIcon, MatBadgeModule, MatDivider, MatTooltip, RouterLink],
})
export class NotificationIndicator implements OnInit {
  private svc = inject(NotificationService);
  private el = inject(ElementRef);

  notifications = this.svc.notifications;
  unreadCount = this.svc.unreadCount;

  open = signal(false);
  loading = signal(false);

  async ngOnInit() {
    await this.reload();
  }

  togglePanel() {
    this.open.update((v) => !v);
  }

  /** Close panel when clicking outside the component. */
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.el.nativeElement.contains(event.target)) {
      this.open.set(false);
    }
  }

  onItemClick(n: Notification) {
    if (!n.read) this.svc.markRead(n.id);
    this.open.set(false);
  }

  toggleRead(n: Notification) {
    n.read ? this.svc.markUnread(n.id) : this.svc.markRead(n.id);
  }

  markAllRead() {
    this.svc.markAllRead();
  }

  // ── display helpers ────────────────────────────────────────────────────────

  getLink(n: Notification): string[] {
    switch (n.type) {
      case 'NEW_POST':
        return ['/posts', n.referenceId];
      default:
        return ['/'];
    }
  }

  getIcon(n: Notification): string {
    switch (n.type) {
      case 'NEW_POST':
        return 'article';
      default:
        return 'notifications';
    }
  }

  getLabel(n: Notification): string {
    switch (n.type) {
      case 'NEW_POST':
        return 'Someone you follow published a post';
      default:
        return 'New notification';
    }
  }

  timeAgo(iso: string): string {
    const diff = Date.now() - new Date(iso).getTime();
    const mins = Math.floor(diff / 60_000);
    if (mins < 1) return 'just now';
    if (mins < 60) return `${mins}m ago`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return `${hrs}h ago`;
    const days = Math.floor(hrs / 24);
    return `${days}d ago`;
  }

  // ── private ────────────────────────────────────────────────────────────────

  private async reload() {
    this.loading.set(true);
    try {
      await this.svc.load();
    } finally {
      this.loading.set(false);
    }
  }
}
