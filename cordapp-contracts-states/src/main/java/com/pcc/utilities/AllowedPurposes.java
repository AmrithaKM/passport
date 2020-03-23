package com.pcc.utilities;

import java.util.ArrayList;
public class AllowedPurposes {
    private enum Purposes {
        Abroad("Abroad"), OutsideKerala("OutsideKerala"), InsideKerala("InsideKerala");
        private String purposes;

        public String getPurposes() {
            return this.purposes;
        }
        Purposes(String purposes) {
            this.purposes = purposes;
        }
    }

   public ArrayList<String> enumFields() {
       Purposes[] purposes = Purposes.values();
       ArrayList<String> purposeList = new ArrayList<String>();
       for (Purposes pur : purposes) {
           purposeList.add(pur.getPurposes().toString());
       }
       return purposeList;
   }

}
