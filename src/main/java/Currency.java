import org.apache.http.client.utils.URIBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Currency {

    private static final String BASE_URL = "https://www.nationalbank.kz/rss/get_rates.cfm";
    private static final List<String> requiredCurrencyNames = List.of("USD", "EUR", "RUB");
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private static String requestRates(){
        try {
            URIBuilder builder = new URIBuilder(BASE_URL);
            builder.addParameter("fdate", SIMPLE_DATE_FORMAT.format(new Date()));
            URL url = new URL(builder.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = input.readLine()) != null) {
                response.append(inputLine);
            }

            input.close();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<Rate> parse(String xml){
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Root root = (Root) jaxbUnmarshaller.unmarshal(new StringReader(xml));
            return root.getItem();
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static List<Rate> getRates() {
        String xml = requestRates();
        if (xml != null) {
            List<Rate> rates = parse(xml);
            if (rates != null) {
                return rates.stream().filter(rate -> isRequiredCurrency(rate.getTitle())).collect(Collectors.toList());
            }
        }
        return null;
    }

    private static boolean isRequiredCurrency(String name) {
        return requiredCurrencyNames.contains(name);
    }

}
