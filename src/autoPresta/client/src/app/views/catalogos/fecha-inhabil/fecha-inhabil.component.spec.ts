import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FechaInhabilComponent } from './fecha-inhabil.component';

describe('FechaInhabilComponent', () => {
  let component: FechaInhabilComponent;
  let fixture: ComponentFixture<FechaInhabilComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FechaInhabilComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FechaInhabilComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
