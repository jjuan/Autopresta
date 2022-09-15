import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MorosidadComponent } from './morosidad.component';

describe('MorosidadComponent', () => {
  let component: MorosidadComponent;
  let fixture: ComponentFixture<MorosidadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MorosidadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MorosidadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
