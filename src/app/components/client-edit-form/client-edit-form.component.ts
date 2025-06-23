import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Employee } from '../../model/Employee';
import { UserService } from '../../services/user.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-client-edit-form',
  templateUrl: './client-edit-form.component.html',
  styleUrl: './client-edit-form.component.css'
})
export class ClientEditFormComponent {
    @Input() client!: Employee;
    @Output() save = new EventEmitter<{client: Employee, password?: string}>();
    @Output() cancel = new EventEmitter<void>();

    

    newPassword: string = '';
    repeatPassword: string = '';
    onSubmit(form: NgForm) {
    if (form.invalid) return;
    
    if (this.newPassword && this.newPassword !== this.repeatPassword) {
      alert("Passwords don't match!");
      return;
    }

    const updatedClient = { ...this.client };
    this.save.emit({
      client: updatedClient,
      ...(this.newPassword ? {password: this.newPassword} : {})
    });
  }

  onCancel() {
    this.cancel.emit();
  }

}
