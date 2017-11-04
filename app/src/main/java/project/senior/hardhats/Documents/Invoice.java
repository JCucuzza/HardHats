package project.senior.hardhats.Documents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import project.senior.hardhats.BackgroundWorkerJSONArray;
import project.senior.hardhats.DataContainer;

/**
 * Created by on 10/12/2017.
 */

public class Invoice {


    private Person customerAddress;
    private Person contractorAddress;
    ArrayList<InvoiceLine> invoiceLines;
    final DecimalFormat df = new DecimalFormat("$0.00");


    Invoice(Person CustomerAddress, Person ContractorAddress, InvoiceLine InvoiceLine)
    {
        customerAddress=CustomerAddress;

        contractorAddress=ContractorAddress;



    }

    public Invoice (String InvoiceId) throws InterruptedException, ExecutionException, JSONException {
        BackgroundWorkerJSONArray getInvoiceData= new BackgroundWorkerJSONArray();
        DataContainer invoiceIdData = new DataContainer();
        invoiceIdData.type = "invoiceexport";
        invoiceIdData.phpVariableNames.add("invoice_id");
        invoiceIdData.dataPassedIn.add(InvoiceId);
        JSONArray returnedData = getInvoiceData.execute(invoiceIdData).get();

        customerAddress=new Person((JSONObject) returnedData.get(1), "Customer");

        contractorAddress=new Person((JSONObject) returnedData.get(2), "Contractor");

        JSONArray returnedInvoiceLines = returnedData.getJSONArray(3);

        invoiceLines = new ArrayList<>();

        for ( int i=0; i<returnedInvoiceLines.length();i++)
        {
            invoiceLines.add(new InvoiceLine(returnedInvoiceLines.getJSONObject(i)));

        }


    }


  //  put linetotal in the invoiceline so we can make totals for each line easily.
    //todo: Tidy up and make look like an invoice. Use finalTotal.
    public String createTxtString()
    {
        StringBuilder invoiceString = new StringBuilder();

        invoiceString.append(contractorAddress.BuildContractorAddress());
        invoiceString.append("\n\n");
        invoiceString.append(customerAddress.BuildCustomerAddress());
        invoiceString.append("\n___________________________________________________________________________\n");
        double finalTotal = 0;
        for (InvoiceLine invoice : invoiceLines)

        {
                finalTotal+=invoice.lineTotal;
                invoiceString.append(invoice.toString());
                invoiceString.append("\n");
        }
        invoiceString.append("___________________________________________________________________________\n");
        invoiceString.append("TOTAL                                              ");
        invoiceString.append(df.format(finalTotal));

        return invoiceString.toString();
    }




}