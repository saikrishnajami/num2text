import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.*;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


class NumtoText{

    private static String[] _to_nineteen = {
        "",
        "one",
        "two",
        "three",
        "four",
        "five",
        "six",
        "seven",
        "eight",
        "nine",
        "ten",
        "eleven",
        "twelve",
        "thirteen",
        "fourteen",
        "fifteen",
        "sixteen",
        "seventeen",
        "eighteen",
        "nineteen",
    };

    private static String[] _tenths = {
        "twenty",
        "thirty",
        "fourty",
        "fifty",
        "sixty",
        "seventy",
        "eighty",
        "ninety"
    };

    private static String[] _places = {
        "",
        "thousand",
        "million",
        "billion"
    };

    // This Function converts a three digit number to text format.
    // expects a string of length atleast 1 and atmost 3 with no trailing zeros to the left as input
    private static String _triplets(String input){
 
        input = input.replaceAll("^0+([1-9]*)", "$1");

        if(input.length() == 0){ return ""; }

        String text_format = "";
        
        // reversing string so that the single, tenth and hundredth places don't change positions if one is missing
        input = new StringBuffer(input).reverse().toString();

        if((input.length() >= 2) && ((input.charAt(1) - '0') > 1)){
            text_format += _tenths[(input.charAt(1) - '0') - 2] + " " + _to_nineteen[input.charAt(0) - '0'];
        }else if(input.length() >= 2){
            text_format += _to_nineteen[Integer.parseInt(String.valueOf(input.charAt(1)) + String.valueOf(input.charAt(0)))];
        }else if(input.length() == 1){
            text_format += _to_nineteen[input.charAt(0) - '0'];
        }

        if(input.length() == 3){
            text_format = _to_nineteen[input.charAt(2) - '0'] + " hundred " + text_format;
        }

        return text_format;
    }

    public static String Convert(String input){
        
        String result = "";
        
        try{
            int tmp_num = Integer.parseInt(input);
            if(tmp_num == 0){
                System.exit(0);
            }
            input = Integer.toString(tmp_num);
            
            for(int i = input.length()-1; i >= 0; i-=3){
                String sub_input = input.substring(Math.max(i - 2, 0), i + 1), tmp_result;
                tmp_result = _triplets(sub_input);
                if(tmp_result.length() > 0)
                    result = tmp_result + " " + _places[(int)(Math.ceil((input.length() - 1) / 3) - Math.ceil(i / 3))] + " " + result;
            }

        }catch(Exception e){
            return "Invalid number";
        }

        return result;
    }

}

class Main{

    public static void main(String args[]) throws IOException
    {
    
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

    }

    static class MyHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange t) throws IOException {


            try{
                String response = "", q_params = t.getRequestURI().getQuery();
                String params[];

                if(q_params == null || q_params.length() == 0){
                    response = "Usage:\n\tServer accepts query parameter named 'n'\n\tExample: localhost:8000?n=1234&n=879";
                }else{

                    if(null == q_params.split("&")){
                        params = new String[]{q_params};
                    }else{
                        params = q_params.split("&");
                    }

                    for(String param: params){
                    
                        String tmp[] = param.split("=");

                        if((tmp.length > 1) && (tmp[0].equals("n"))){
                            response += NumtoText.Convert(tmp[1]) + "\n";
                        }

                    }
                }

                if(response.length() == 0){
                    response = "Usage:\n\tServer accepts query parameter named 'n'\n\tExample: localhost:8000?n=1234&n=879";
                }

                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }catch(Exception err){
                System.out.println(err);
            }

        }
    }

}
