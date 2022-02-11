package com.proteam.showroomaudit.views.activities;

public class AuditiinfofromDatabase {

    String user_id,audit_code,rack_code, item_code,valid_invalid,sys_created_on, item_qty;

    public AuditiinfofromDatabase(String user_id, String audit_code, String rack_code, String item_code, String valid_invalid, String sys_created_on, String item_qty) {
        this.user_id = user_id;
        this.audit_code = audit_code;
        this.rack_code = rack_code;
        this.item_code = item_code;
        this.valid_invalid = valid_invalid;
        this.sys_created_on = sys_created_on;
        this.item_qty = item_qty;
    }
}
