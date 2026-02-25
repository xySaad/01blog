import {
  Component,
  contentChildren,
  ElementRef,
  inject,
  input,
  signal,
  viewChild,
} from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Directive, Input } from '@angular/core';

@Component({
  selector: 'carousel',
  templateUrl: `carousel.html`,

  styleUrl: 'carousel.css',
  imports: [MatIconButton, MatIcon],
})
export class Carousel {
  scrollContainer = viewChild.required<ElementRef<HTMLElement>>('scrollContainer');

  steps = input(1);

  readonly skipDirectives = contentChildren(CarouselSkipDirective);
  isOverflowing = signal(false);
  private resizeObserver: ResizeObserver;

  constructor() {
    this.resizeObserver = new ResizeObserver((entries) => {
      const el = entries[0].target;
      this.isOverflowing.set(el.scrollWidth > el.clientWidth);
    });
  }
  ngAfterViewInit() {
    this.resizeObserver.observe(this.scrollContainer().nativeElement);
  }

  ngOnDestroy() {
    this.resizeObserver.disconnect();
  }

  navigate(direction: 'prev' | 'next') {
    const nextItem = this.findNextItem(direction);

    nextItem?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }

  //TOOD: optimize by caching currentIndex and re-calculate only when manually dragged
  findNextItem(direction: 'prev' | 'next'): HTMLElement | null {
    const el = this.scrollContainer().nativeElement;
    const children = Array.from(el.children) as HTMLElement[];
    const skipDirectives = this.skipDirectives();

    const skipMap = new Map<HTMLElement, CarouselSkipDirective>(
      skipDirectives.map((d) => [d.elementRef.nativeElement, d]),
    );
    const navigable = children.filter((child) => !skipMap.get(child)?.skip());
    const len = navigable.length;

    if (direction === 'prev') {
      const mostLeftInView = navigable.findIndex((c) => this.isInView(c));
      const prevIndex = mostLeftInView - this.steps();
      if (prevIndex < 0) return navigable[0];
      return navigable[prevIndex];
    }

    const mostRightInView = navigable.findLastIndex((c) => this.isInView(c));
    const nextIndex = mostRightInView + this.steps();
    if (nextIndex >= len) return navigable[len - 1];

    return navigable[nextIndex];
  }

  isInView(child: HTMLElement): boolean {
    const el = this.scrollContainer().nativeElement;
    const containerRect = el.getBoundingClientRect();
    const childRect = child.getBoundingClientRect();

    return (
      Math.round(childRect.left) >= Math.round(containerRect.left) &&
      Math.round(childRect.right) <= Math.round(containerRect.right)
    );
  }
}

@Directive({
  selector: '[carouselSkip]',
})
export class CarouselSkipDirective {
  readonly elementRef = inject<ElementRef<HTMLElement>>(ElementRef);
  readonly skip = input.required<boolean>({ alias: 'carouselSkip' });
}
