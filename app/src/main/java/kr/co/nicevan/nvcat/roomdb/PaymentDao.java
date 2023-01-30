package kr.co.nicevan.nvcat.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PaymentDao {

    @Query("SELECT * FROM Payment")
    List<Payment> getAllPayment();

    @Query("SELECT * FROM Payment WHERE uid IN (:uid)")
    List<Payment> loadById(int[] uid);

    @Insert
    void insertPayment(Payment payment);

    @Delete
    void deletePayment(Payment payment);

    @Update
    void updatePayment(Payment payment);
}
