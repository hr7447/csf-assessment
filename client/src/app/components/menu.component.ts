import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem, OrderItem } from '../models';
import { RestaurantService } from '../restaurant.service';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {
  // TODO: Task 2
  menuItems: MenuItem[] = [];
  selectedItems: OrderItem[] = [];
  totalAmount: number = 0;
  
  constructor(
    private restaurantSvc: RestaurantService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.loadMenuItems();
  }
  
  async loadMenuItems() {
    try {
      this.menuItems = await this.restaurantSvc.getMenuItems();
      // Sort by name in ascending order
      this.menuItems.sort((a, b) => a.name.localeCompare(b.name));
    } catch (error) {
      console.error('Error loading menu items:', error);
    }
  }
  
  addItem(item: MenuItem) {
    const existingItem = this.selectedItems.find(i => i.id === item._id);
    if (existingItem) {
      existingItem.quantity++;
    } else {
      this.selectedItems.push({
        id: item._id,
        name: item.name,
        price: item.price,
        quantity: 1
      });
    }
    this.updateTotal();
  }
  
  removeItem(itemId: string) {
    const index = this.selectedItems.findIndex(i => i.id === itemId);
    if (index >= 0) {
      if (this.selectedItems[index].quantity > 1) {
        this.selectedItems[index].quantity--;
      } else {
        this.selectedItems.splice(index, 1);
      }
      this.updateTotal();
    }
  }
  
  updateTotal() {
    this.totalAmount = this.selectedItems.reduce(
      (sum, item) => sum + (item.price * item.quantity), 0
    );
  }
  
  getSelectedQuantity(itemId: string): number {
    const item = this.selectedItems.find(i => i.id === itemId);
    return item ? item.quantity : 0;
  }
  
  placeOrder() {
    console.log("Place order button clicked");
    if (this.selectedItems.length > 0) {
      console.log("Navigating to place-order with", this.selectedItems.length, "items");
      
      // First store the data in sessionStorage as a backup
      sessionStorage.setItem('selectedItems', JSON.stringify(this.selectedItems));
      sessionStorage.setItem('totalAmount', this.totalAmount.toString());
      
      // Then navigate with state
      this.router.navigate(['/place-order'], { 
        state: { 
          selectedItems: this.selectedItems,
          totalAmount: this.totalAmount
        } 
      }).then(success => {
        console.log("Navigation success:", success);
      }).catch(error => {
        console.error("Navigation error:", error);
      });
    } else {
      console.log("No items selected, not navigating");
    }
  }
}
