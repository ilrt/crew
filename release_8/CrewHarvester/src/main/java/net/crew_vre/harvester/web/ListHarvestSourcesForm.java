package net.crew_vre.harvester.web;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ListHarvestSourcesForm.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class ListHarvestSourcesForm {

    public ListHarvestSourcesForm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(String deleteButton) {
        this.deleteButton = deleteButton;
    }

    public String getEditButton() {
        return editButton;
    }

    public void setEditButton(String editButton) {
        this.editButton = editButton;
    }

    public String getAddButton() {
        return addButton;
    }

    public void setAddButton(String addButton) {
        this.addButton = addButton;
    }

    public String getHarvestButton() {
        return harvestButton;
    }

    public void setHarvestButton(String harvestButton) {
        this.harvestButton = harvestButton;
    }

    private String id;
    private String deleteButton;
    private String editButton;
    private String addButton;
    private String harvestButton;

}
