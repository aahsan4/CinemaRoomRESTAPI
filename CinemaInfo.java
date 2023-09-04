package cinema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class CinemaInfo {
    private int total_rows;
    private int total_columns;
    private List<Seat> available_seats;

    @JsonProperty("total_rows")
    public int getTotalRows() {
        return this.total_rows;
    }
    @JsonProperty("total_columns")
    public int getTotalColumns() {
        return this.total_columns;
    }
    @JsonProperty("available_seats")
    public List<Seat> getAvailableSeats() {
        return this.available_seats;
    }

    public void setTotalRows(int total_rows) {
        this.total_rows = total_rows;
    }

    public void setTotalColumns(int total_columns) {
        this.total_columns = total_columns;
    }

    public void setAvailableSeats(List<Seat> available_seats) {
        this.available_seats = available_seats;
    }


}
