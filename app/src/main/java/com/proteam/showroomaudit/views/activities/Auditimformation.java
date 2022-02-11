package com.proteam.showroomaudit.views.activities;

public class Auditimformation {

    String user_id,audit_code,rack_code,total_racks,racks_completed,total_itemqty,total_untagged,doc_name,rack_gc_count,actual_gc_count;

    public Auditimformation(String user_id, String audit_code, String rack_code, String total_racks, String racks_completed, String total_itemqty, String total_untagged, String doc_name, String rack_gc_count, String actual_gc_count) {
        this.user_id = user_id;
        this.audit_code = audit_code;
        this.rack_code = rack_code;
        this.total_racks = total_racks;
        this.racks_completed = racks_completed;
        this.total_itemqty = total_itemqty;
        this.total_untagged = total_untagged;
        this.doc_name = doc_name;
        this.rack_gc_count = rack_gc_count;
        this.actual_gc_count = actual_gc_count;
    }
}
