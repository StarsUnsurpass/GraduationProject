package com.graduationproject.power_fault_analysis.dto;

public class EntityRenameRequest {
    private String label;
    private String oldName;
    private String newName;

    public EntityRenameRequest() {}

    public EntityRenameRequest(String label, String oldName, String newName) {
        this.label = label;
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
