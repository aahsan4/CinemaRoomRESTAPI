package cinema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.*;

@RestController

public class CinemaController {

    private static final int TOTAL_ROWS = 9;
    private static final int TOTAL_COLUMNS = 9;

    private List<Seat> availableSeats = generateAvailableSeats();
    private Map<String, Ticket> purchasedTickets = new HashMap<>(); // Using a Map to store purchased tickets with tokens
    private int totalIncome = 0; // Tracking the total income

    @GetMapping("/seats")
    public CinemaInfo getAvailableSeats() {
        CinemaInfo cinemaInfo = new CinemaInfo();
        cinemaInfo.setTotalRows(TOTAL_ROWS);
        cinemaInfo.setTotalColumns(TOTAL_COLUMNS);
        cinemaInfo.setAvailableSeats(availableSeats);

        return cinemaInfo;
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTicket(@RequestBody TicketRequest request) {
        int requestedRow = request.getRow();
        int requestedColumn = request.getColumn();

        if (isOutOfBounds(requestedRow, requestedColumn)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("The number of a row or a column is out of bounds!"));
        }

        Seat seatToPurchase = availableSeats.stream()
                .filter(seat -> seat.getRow() == requestedRow && seat.getColumn() == requestedColumn)
                .findFirst()
                .orElse(null);

        if (seatToPurchase == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("The ticket has been already purchased!"));
        }

        int price = seatToPurchase.getPrice();
        availableSeats.remove(seatToPurchase);
        // Generating a unique token for the purchased ticket
        String token = UUID.randomUUID().toString();

        // Creating a Ticket object and add it to the map of purchased tickets
        Ticket purchasedTicket = new Ticket(seatToPurchase.getRow(), seatToPurchase.getColumn(), price);
        purchasedTickets.put(token, purchasedTicket);

        PurchaseResponse response = new PurchaseResponse(token, purchasedTicket);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnTicket(@RequestBody TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();

        // Finding the ticket with the given token
        Ticket ticketToReturn = purchasedTickets.get(token);

        if (ticketToReturn == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Wrong token!"));
        }

        // Refunding the ticket by making the seat available again
        availableSeats.add(new Seat(ticketToReturn.getRow(), ticketToReturn.getColumn(), ticketToReturn.getPrice()));
        purchasedTickets.remove(token);

        ReturnResponse response = new ReturnResponse(ticketToReturn);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics(@RequestParam Map<String, String> params) {
        if (params.containsKey("password") && "super_secret".equals(params.get("password"))) {
            int numberOfAvailableSeats = availableSeats.size();
            int numberOfPurchasedTickets = purchasedTickets.size();

            // Calculating current income based on purchased tickets
            int currentIncome = purchasedTickets.values().stream()
                    .mapToInt(Ticket::getPrice)
                    .sum();
            totalIncome += currentIncome; // Updating total income
            StatisticsResponse response = new StatisticsResponse(currentIncome, numberOfAvailableSeats, numberOfPurchasedTickets);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("The password is wrong!"));
        }
    }
    private List<Seat> generateAvailableSeats() {
        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= TOTAL_ROWS; row++) {
            for (int column = 1; column <= TOTAL_COLUMNS; column++) {
                int price = calculatePriceForSeat(row);
                seats.add(new Seat(row, column, price));
            }
        }
        return seats;
    }

    private int calculatePriceForSeat(int row) {

        return row <= 4 ? 10 : 8;
    }
    private boolean isOutOfBounds(int row, int column) {
        return row < 1 || row > TOTAL_ROWS || column < 1 || column > TOTAL_COLUMNS;
    }
}


