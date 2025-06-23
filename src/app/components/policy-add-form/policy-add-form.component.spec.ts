import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PolicyAddFormComponent } from './policy-add-form.component';

describe('PolicyAddFormComponent', () => {
  let component: PolicyAddFormComponent;
  let fixture: ComponentFixture<PolicyAddFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PolicyAddFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PolicyAddFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
