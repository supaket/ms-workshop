import { Router } from '@angular/router';
import { OnInit, Component } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { CookieService } from 'ngx-cookie-service';
@Component({
  selector:'order_summary',
  templateUrl:'./ordersummary.component.html'
})
export class OrderSummary implements OnInit{
  orderSummary:boolean;
  deliveryAddress:boolean;
  paymentOptions:boolean;
  data:any;
  total:any;
  paymentOptionsData:any;
  addressLabelData:any;
  shippingAddress:ShippingAddress
  payment:Payment;
  msgData: any = [];
  showErrorDialogue: boolean;
  number: number;
  msg:any;
  showOrdersDialogue:boolean;
  discountApplied:boolean;
  afterDiscountAmount:number;
  constructor(private router:Router,private httpclient:HttpClient,private cookieService: CookieService){
    this.shippingAddress=new ShippingAddress();
    this.payment=new Payment();
    this.orderSummary=true;
    this.deliveryAddress=false;
    this.paymentOptions=false;
    this.payment.mode='1';
    this.shippingAddress.label='1';

    this.paymentOptionsData=[{
      "name":"Net Banking",
      "id":"1"
    },{
      "name":"Debit Card",
      "id":"2"
    },{
      "name":"Credit Card",
      "id":"3"
    },{
      "name":"UPI",
      "id":"4"
    }];
    this.addressLabelData=[{
      "name":"Home",
      "id":"1"
    },{
      "name":"Office",
      "id":"2"
    }];
    this.fetchOrderDetails();
  }

  // THIS METHOD IS USED FOR Delivery Address AND REST CALL FOR SAVING DATA
  onOrderSummayNextClick(){
    this.orderSummary=false;
    this.deliveryAddress=true;
    this.paymentOptions=false;
  }

  //THIS METHOD IS USED FOR ENABLE PAYMENT OPTIONS AND REST CALL FOR SAVING DATA
  onDeliveryNextClick(){
  this.validateShippingAddress();
  if(this.msgData && this.msgData.length==0){
   this.addShippingAddress();
  }
 }

   //THIS METHOD IS USED FOR SELECT PAYMNET MODE
   setSelectedPaymentOptions(data:any){
    if(data.id){
      this.payment.mode=data.id;
    }
  }

  //THIS METHOD IS USED FOR DELIVERY ADDRESS LABEL SET
  setSelectedLabel(data:any){
  if(data.id){
    this.shippingAddress.label=data.id;
  }
  }

   //Method to close Error Dialogue box
   close() {
    this.showErrorDialogue = false;
    this.msgData = [];
}
  //THiS IS METHOD IS VALIDATING SHIPPING ADDRESS MODEL
  validateShippingAddress(){
    if(this.shippingAddress.address=='' || this.shippingAddress.address==null){
      this.msgData.push('Address should not be blank');
      this.showErrorDialogue=true;
    }
    if(this.shippingAddress.city=='' || this.shippingAddress.city==null){
      this.msgData.push('Please select city.');
      this.showErrorDialogue=true;
    }
    if(this.shippingAddress.country=='' || this.shippingAddress.country==null){
      this.msgData.push('Please select country');
      this.showErrorDialogue=true;
    }
    if(this.shippingAddress.province=='' || this.shippingAddress.province==null){
      this.msgData.push('Please select province');
      this.showErrorDialogue=true;
    }
    if(this.shippingAddress.postalcode=='' || this.shippingAddress.postalcode==null){
      this.msgData.push('Postal code should not be blank');
      this.showErrorDialogue=true;
    }else if(this.shippingAddress.postalcode.length<6 || this.shippingAddress.postalcode.length>10){
      this.msgData.push('Postal code should be in between 6 to 10 characters');
      this.showErrorDialogue=true;
    }
  }
  //THIS METHOD IS USED FOR PAYMENT OPTIONS DONE AND REDIRECT TO ORDER
  onPaymentOptionNextClick(){
    let responseData:any;
    const headers = new HttpHeaders().append('Content-Type', 'application/json;charset=UTF-8').append('tokenid', this.cookieService.get('tokenid'));

    if(this.payment.mode){
      this.httpclient.post('api/od/order/write/addpaymentdetails',this.payment,{headers}).subscribe(
        (response)=>{
          responseData=response;
        },
        (error)=>{
          this.msgData.push('Unable to connect to server.');
          this.showErrorDialogue = true;
        },
        ()=>{
          if(responseData.success){
            this.showOrdersDialogue=true;
          }
        }
      )
    }

  }

  // VALIDATE AND SAVE SHIPPING ADDRESS
  addShippingAddress(){
    let responseData:any;
    const headers = new HttpHeaders().append('Content-Type', 'application/json;charset=UTF-8').append('tokenid', this.cookieService.get('tokenid'));

    if(this.shippingAddress){
      this.httpclient.post('api/od/order/write/addshippingaddress',this.shippingAddress,{headers}).subscribe(
        (response)=>{
          responseData=response;
        },
        (error)=>{
          this.msgData.push('Unable to connect to server.');
          this.showErrorDialogue = true;
        },
        ()=>{
          if(responseData.success){
            this.orderSummary=false;
            this.deliveryAddress=false;
            this.paymentOptions=true;
          }
        }
      )
    }
  }


//THIS METHOD IS USED FOR GETTING ORDER ID
fetchOrderDetails(){
  const headers = new HttpHeaders().append('Content-Type', 'application/json;charset=UTF-8').append('tokenid',this.cookieService.get('tokenid'));

  let responseData:any;
   this.httpclient.get('api/od/order/query/getOrderDetails',{headers}).subscribe(
     response=>{
       responseData=response;
     },
     error=>{
       this.msgData.push('Unable to connect to server.');
       this.showErrorDialogue = true;
     },
     () => {
       if(responseData){
        this.payment.orderId =responseData.response.orderId;
        this.shippingAddress.orderId =responseData.response.orderId;
        if (responseData.response && responseData.response.lineItems && responseData.response.lineItems.length > 0) {
         this.data = responseData.response.lineItems;
         this.number = responseData.response.lineItems.length;
         if(responseData.response.moneytoryValue){
          this.afterDiscountAmount = responseData.response.moneytoryValue.total;
         }
         if(responseData.response.total){
          this.total=responseData.response.total;
         }
         if(responseData.response.discountApplied){
           this.discountApplied=true;
         }

     }
     else {
         this.number = 0;
     }
       }

     }
   );
 }


//Method that navigates to order screen
showOrders() {
  this.router.navigate(['home/order']);
}
 //Method to close the Dialogue box
 cancel() {
  this.showOrdersDialogue = false;
}
ngOnInit(){

}
}


export class Payment{
  mode:any;
  orderId:any;
}

export class ShippingAddress{
  orderId:any;
  label:any;
  address:any;
  country:any;
  province:any;
  postalcode:any;
  city:any;
}


