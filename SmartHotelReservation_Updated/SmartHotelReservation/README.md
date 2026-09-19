# Smart Hotel Reservation — Room Photos, Services & Ratings

This version extends the existing JavaFX + SQLite Smart Hotel application.

## Added
- Admin can add/change a photo for every room.
- Customer can select/double-click a room and open a room photo/details window.
- Customer sees price per night and calculated total room cost for the selected stay.
- Customer can book an available room.
- Customer can select extra services such as Laundry, Swimming Pool, Gym and Food.
- Extra service requests start as PENDING.
- Admin can APPROVE or REJECT each service and send a message to the customer.
- Approved service charges are automatically added to the reservation total.
- Customer can see service status and admin message.
- Customer can submit one 1–5 star rating/comment per reservation.
- Admin has a Ratings tab to view all submitted ratings and comments.
- Existing SQLite databases are migrated automatically with the new room-photo and service-request fields.

## Default admin
- Username: `pratik`
- Password: `pratik123`

## Run
Open the `SmartHotelReservation` Maven project in IntelliJ IDEA with a Java/JDK version compatible with the project's Maven configuration, then run `com.smarthotel.Main`.
