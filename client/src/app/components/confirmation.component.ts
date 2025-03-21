import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OrderConfirmation } from '../models';

@Component({
  selector: 'app-confirmation',
  standalone: false,
  templateUrl: './confirmation.component.html',
  styleUrl: './confirmation.component.css'
})
export class ConfirmationComponent implements OnInit {

  // TODO: Task 5
  confirmation: OrderConfirmation | null = null;
  formattedDate: string = '';
  
  constructor(private router: Router) {}
  
  ngOnInit(): void {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state as { confirmation: OrderConfirmation };
    
    if (state && state.confirmation) {
      this.confirmation = state.confirmation;
      this.formatDate();
    } else {
      // Try to get from history state
      const history = window.history.state;
      if (history && history.confirmation) {
        this.confirmation = history.confirmation;
        this.formatDate();
      } else {
        // Try to get from sessionStorage
        const storedConfirmation = sessionStorage.getItem('orderConfirmation');
        if (storedConfirmation) {
          this.confirmation = JSON.parse(storedConfirmation);
          this.formatDate();
          console.log("Retrieved confirmation from sessionStorage");
        } else {
          // If no confirmation data is present, redirect to menu
          console.log("No confirmation data found, redirecting to menu");
          this.router.navigate(['/']);
        }
      }
    }
  }
  
  private formatDate(): void {
    if (this.confirmation) {
      // Format the timestamp to a readable date
      this.formattedDate = new Date(this.confirmation.timestamp).toLocaleDateString();
    }
  }
  
  goBack(): void {
    // Clear session storage and navigate back to menu
    sessionStorage.removeItem('orderConfirmation');
    this.router.navigate(['/']);
  }
}
