import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormFechaInhabilComponent } from './form-fecha-inhabil.component';

describe('FormFechaInhabilComponent', () => {
  let component: FormFechaInhabilComponent;
  let fixture: ComponentFixture<FormFechaInhabilComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormFechaInhabilComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FormFechaInhabilComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
