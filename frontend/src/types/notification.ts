export interface Notification {
  id: string;
  userId: string;
  type: 'NEW_POST' | string;
  referenceId: string;
  createdAt: string;
  read: boolean;
}
