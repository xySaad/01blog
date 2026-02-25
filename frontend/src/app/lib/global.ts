import { User } from '../../types/user';
import { API } from './api';
import { snake2StartCase } from './fmt';

export const global = {
  user: new User(),
  reportReasons: await getReportReasons(),
};

async function getReportReasons() {
  const reportReasons = await API.get<string[]>(`/report`);
  return reportReasons.map((r) => ({ text: snake2StartCase(r), value: r }));
}
