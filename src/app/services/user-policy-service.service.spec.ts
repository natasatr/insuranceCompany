import { TestBed } from '@angular/core/testing';

import { UserPolicyServiceService } from './user-policy-service.service';

describe('UserPolicyServiceService', () => {
  let service: UserPolicyServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserPolicyServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
