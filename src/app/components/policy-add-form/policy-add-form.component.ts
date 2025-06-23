import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Policy } from '../../model/Policy';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-policy-add-form',
  templateUrl: './policy-add-form.component.html',
  styleUrl: './policy-add-form.component.css'
})
export class PolicyAddFormComponent {
    @Input() policy!: Policy;
    @Output() save = new EventEmitter<{policy: Policy, password?: string}>();
    @Output() cancel = new EventEmitter<void>();

    

    onSubmit(form: NgForm) {
    if (form.invalid) return;
    this.save.emit({
      policy: this.policy
    });
  }

  onCancel() {
    this.cancel.emit();
  }
}
