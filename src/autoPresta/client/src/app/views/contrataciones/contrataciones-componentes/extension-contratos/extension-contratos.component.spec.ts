import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExtensionContratosComponent } from './extension-contratos.component';

describe('ExtensionContratosComponent', () => {
  let component: ExtensionContratosComponent;
  let fixture: ComponentFixture<ExtensionContratosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExtensionContratosComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExtensionContratosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
