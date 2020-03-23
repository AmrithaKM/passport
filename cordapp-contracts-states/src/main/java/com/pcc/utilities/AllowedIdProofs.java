package com.pcc.utilities;

import java.util.ArrayList;

public class AllowedIdProofs {
    private enum IdProofs {
        Passport("Passport"),
        Adhaar("Adhaar"),
        DrivingLicense("DrivingLicense"),
        VoterID("VoterID"),
        ElectricityBill("ElectricityBill");
        
		private String idProofs;

        public String getIdProofs() {
            return this.idProofs;
        }
        IdProofs(String idProofs) {
            this.idProofs = idProofs;
        }
    }

   public ArrayList<String> enumFields() {
       IdProofs[] idProofs = IdProofs.values();
       ArrayList<String> idProofList = new ArrayList<String>();
       for (IdProofs idProof : idProofs) {
           idProofList.add(idProof.getIdProofs().toString());
       }
       return idProofList;
   }

}
