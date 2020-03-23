package com.pcc.utilities;

import java.util.ArrayList;

public class AllowedAddressProofs {
    private enum AddressProofs {
        RationCard("RationCard"), Adhaar("Adhaar"), VoterID("VoterID"), SSLCBook("SSLCBook"), BankAccountPassbook("BankAccountPassbook");
        
		private String addressProofs;

        public String getAddressProofs() {
            return this.addressProofs;
        }
        AddressProofs(String addressProofs) {
            this.addressProofs = addressProofs;
        }
    }

   public ArrayList<String> enumFields() {
       AddressProofs[] addressProofs = AddressProofs.values();
       ArrayList<String> addressProofList = new ArrayList<String>();
       for (AddressProofs addressProof : addressProofs) {
           addressProofList.add(addressProof.getAddressProofs().toString());
       }
       return addressProofList;
   }

}
