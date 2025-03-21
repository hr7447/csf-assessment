import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderItem, Order, OrderConfirmation } from '../models';
import { RestaurantService } from '../restaurant.service';

@Component({
  selector: 'app-place-order',
  standalone: false,
  templateUrl: './place-order.component.html',
  styleUrl: './place-order.component.css'
})
export class PlaceOrderComponent implements OnInit {

  // TODO: Task 3
  orderForm: FormGroup;
  selectedItems: OrderItem[] = [];
  totalAmount: number = 0;
  error: string = '';
  
  constructor(
    private fb: FormBuilder,
    private restaurantSvc: RestaurantService,
    private router: Router
  ) {
    // Initialize the form in the constructor to avoid any timing issues
    this.orderForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    // Try to get the state from history
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state as { 
      selectedItems: OrderItem[], 
      totalAmount: number 
    };

    if (state) {
      this.selectedItems = state.selectedItems;
      this.totalAmount = state.totalAmount;
    }
  }
  
  ngOnInit(): void {
    // Check if we have items, if not try to get them from history state
    if (this.selectedItems.length === 0) {
      const history = window.history.state;
      if (history && history.selectedItems) {
        this.selectedItems = history.selectedItems;
        this.totalAmount = history.totalAmount;
      } else {
        // Try to get from sessionStorage
        const storedItems = sessionStorage.getItem('selectedItems');
        const storedTotal = sessionStorage.getItem('totalAmount');
        
        if (storedItems && storedTotal) {
          this.selectedItems = JSON.parse(storedItems);
          this.totalAmount = parseFloat(storedTotal);
          console.log("Retrieved items from sessionStorage:", this.selectedItems.length);
        } else {
          // No items were selected, redirect back to menu
          console.log("No items found in state or sessionStorage, redirecting to menu");
          this.router.navigate(['/']);
        }
      }
    }
  }
  
  startOver(): void {
    // Clear session storage and navigate back
    sessionStorage.removeItem('selectedItems');
    sessionStorage.removeItem('totalAmount');
    this.router.navigate(['/']);
  }
  
  async confirmOrder(): Promise<void> {
    if (this.orderForm.invalid) {
      return;
    }
    
    const { username, password } = this.orderForm.value;
    
    const order: Order = {
      username,
      password,
      items: this.selectedItems.map(item => ({
        id: item.id,
        price: item.price,
        quantity: item.quantity
      }))
    };
    
    try {
      const confirmation = await this.restaurantSvc.placeOrder(order);
      // Clear session storage before navigation
      sessionStorage.removeItem('selectedItems');
      sessionStorage.removeItem('totalAmount');
      
      // Store confirmation in sessionStorage as backup
      sessionStorage.setItem('orderConfirmation', JSON.stringify(confirmation));
      
      // Navigate to confirmation view with the order confirmation
      this.router.navigate(['/confirmation'], { 
        state: { confirmation }
      });
    } catch (error: any) {
      console.error('Order placement error:', error);
      if (error.status === 401) {
        this.error = 'Invalid username and/or password';
      } else {
        this.error = error.error?.message || 'An error occurred while placing your order';
      }
      // Display error using alert
      alert(this.error);
    }
  }
}
