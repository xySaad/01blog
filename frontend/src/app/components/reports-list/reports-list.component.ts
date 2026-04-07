import { Component, signal } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule } from '@angular/material/list';
import { Report, UserReport } from '../../../types/Report';
import { Collection } from '../../../types/collection';
import { API } from '../../lib/api';
import { global } from '../../lib/global';
import { requiredSelect } from '../../lib/requiredSelect';
import { Carousel, CarouselSkipDirective } from '../carousel/carousel.component';
import { ReportCard } from '../report-card/report-card.component';
import { SelectList, SelectOption } from '../select-list.component';

@Component({
  selector: 'reports-list',
  styleUrl: 'reports-list.css',
  templateUrl: 'reports-list.html',
  imports: [
    Carousel,
    MatListModule,
    SelectList,
    SelectOption,
    CarouselSkipDirective,
    MatExpansionModule,
    ReportCard,
  ],
})
export class ReportsList {
  reports = signal<Report[]>([]);
  reportReasons = global.reportReasons;
  selectedReason = requiredSelect('all');

  constructor() {
    API.getH(Collection(Report), '/moderation/reports').then(this.reports.set);
  }
}
