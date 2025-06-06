package org.isu_std.admin.admin_main.admin_requested_documents;

import org.isu_std.admin.admin_main.AdminSection;
import org.isu_std.io.SystemInput;
import org.isu_std.io.Util;

public class RequestedDocument implements AdminSection{
    private final RequestedDocumentController reqDocController;
    private final String[] REQUEST_VIEW_CONTENTS = {
            "Approve the Request", "Decline the Request", "Display Requested Document",
            "Display User Details", "View Requirement Files", "Return to Choose Requested Docs"
    };

    protected RequestedDocument(RequestedDocumentController reqDocController){
        this.reqDocController = reqDocController;
    }

    @Override
    public void run(String sectionTitle) {
        while (true) {
            Util.printSectionTitle(sectionTitle);

            if (!reqDocController.isThereExistingRequest()) {
                return;
            }

            if (!setRequestedDocChoice()) {
                return;
            }

            docRequestValidationProcess();
        }
    }

    private boolean setRequestedDocChoice(){
        int backLengthValue = reqDocController.getReqDocListLength() + 1;
        reqDocController.printRequestedDocs();
        Util.printChoice("%d. Back to Menu.".formatted(backLengthValue));

        while(true){
            int docChoice = SystemInput.getIntChoice("Enter Document No. you want to view : ", backLengthValue);

            if(docChoice == backLengthValue){ // Back Statement
                return false;
            }

            if(reqDocController.setDocumentReqChoice(docChoice)){
                return true;
            }
        }
    }

    private void docRequestValidationProcess(){
        while(true){
            Util.printSectionTitle(reqDocController.getDocReqSectionTitle());
            Util.printChoices(REQUEST_VIEW_CONTENTS);

            int choice = SystemInput.getIntChoice("Enter your choice : ", REQUEST_VIEW_CONTENTS.length);

            if(choice == REQUEST_VIEW_CONTENTS.length){
                return;
            }

            if(reqDocController.isReqValidationFinish(choice)){
                return;
            }
        }
    }
}
