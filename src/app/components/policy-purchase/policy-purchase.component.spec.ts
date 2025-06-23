import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PolicyPurchaseComponent } from './policy-purchase.component';

describe('PolicyPurchaseComponent', () => {
  let component: PolicyPurchaseComponent;
  let fixture: ComponentFixture<PolicyPurchaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PolicyPurchaseComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PolicyPurchaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
