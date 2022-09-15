import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AsignarMontosComponent } from './asignar-montos.component';

describe('AsignarMontosComponent', () => {
  let component: AsignarMontosComponent;
  let fixture: ComponentFixture<AsignarMontosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AsignarMontosComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AsignarMontosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
