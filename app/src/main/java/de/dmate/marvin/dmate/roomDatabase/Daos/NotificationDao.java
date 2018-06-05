package de.dmate.marvin.dmate.roomDatabase.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface NotificationDao {

    //get all notifications by userId
    @Query("SELECT * FROM notifications WHERE uIdF = :userId ORDER BY timestamp DESC")
    LiveData<List<Notification>> getAllNotificationsByUserId(Integer userId);

    @Query("SELECT * FROM notifications WHERE nId = :notificationId")
    Notification getNotificationById(Integer notificationId);

    @Insert(onConflict = REPLACE)
    void insertNotification(Notification notification);

    @Delete
    void deleteNotification(Notification notification);

}
