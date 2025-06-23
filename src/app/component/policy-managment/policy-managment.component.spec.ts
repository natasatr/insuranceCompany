import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PolicyManagmentComponent } from './policy-managment.component';

describe('PolicyManagmentComponent', () => {
  let component: PolicyManagmentComponent;
  let fixture: ComponentFixture<PolicyManagmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PolicyManagmentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PolicyManagmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
