package com.pcc.utilities;

import java.util.ArrayList;

public class AllowedRoles {
    private enum Roles {
        Commissioner("Commissioner"),
		DSBOSHO("DSBOCPO"), // not SHO but CPO
        DCRBCPO("DCRBCPO"),
        //DSBOFVO("DSBOFVO"),
        DSBODCP("DSBODCP"),
        DSBOACP("DSBOACP"),
        FVO("FVO");

        private String roles;

        public String getRoles() {
            return this.roles;
        }
        Roles(String roles) {
            this.roles = roles;
        }
    }

   public ArrayList<String> enumFields() {
       Roles[] roles = Roles.values();
       ArrayList<String> roleList = new ArrayList<String>();
       for (Roles role : roles) {
           roleList.add(role.getRoles().toString());
       }
       return roleList;
   }

}
