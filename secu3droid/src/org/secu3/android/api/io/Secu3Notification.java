/* Secu3Droid - An open source, free manager for SECU-3 engine
 * control unit
 * Copyright (C) 2013 Maksim M. Levin. Russia, Voronezh
 * 
 * SECU-3  - An open source, free engine control unit
 * Copyright (C) 2007 Alexey A. Shabelnikov. Ukraine, Gorlovka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * contacts:
 *            http://secu-3.org
 *            email: mmlevin@mail.ru
*/

package org.secu3.android.api.io;

import org.secu3.android.MainActivity;
import org.secu3.android.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class Secu3Notification {
	public Notification secu3Notification;
	public Notification connectionProblemNotification;
	public Notification serviceStoppedNotification;
	public NotificationManager notificationManager;
	
	private Context ctx;
	
	public Secu3Notification(Context ctx) {
		this.ctx = ctx;
		notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);			
		
		secu3Notification = new NotificationCompat.Builder(ctx)
			.setContentTitle(ctx.getString(R.string.foreground_service_started_notification_title))
			.setSmallIcon(R.drawable.ic_launcher)											
			.setWhen(System.currentTimeMillis())
			.setOngoing(true)
			.setContentIntent(PendingIntent.getActivity(ctx, 0, new Intent (ctx,MainActivity.class), Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT))
			.build();					
		
		connectionProblemNotification = new NotificationCompat.Builder(ctx)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentIntent(PendingIntent.getActivity(ctx, 0, new Intent (ctx,MainActivity.class), Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT))
			.build();		

		serviceStoppedNotification = new NotificationCompat.Builder(ctx)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentIntent(PendingIntent.getActivity(ctx, 0, new Intent (ctx,MainActivity.class), Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT))
			.build();		
	}
	
	public void notifyConnectionProblem (int maxConnectionRetries, int nbRetriesRemaining) {
		String pbMessage = ctx.getResources().getQuantityString(R.plurals.connection_problem_notification, nbRetriesRemaining, nbRetriesRemaining);
		
		connectionProblemNotification = new NotificationCompat.Builder (ctx)		
				.setContentTitle(ctx.getString(R.string.connection_problem_notification_title))
				.setContentText(pbMessage)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_launcher)
				.setOngoing(true)
				.setContentIntent(connectionProblemNotification.contentIntent)
				.setNumber(1 + maxConnectionRetries - nbRetriesRemaining)
				.build();				
		notificationManager.notify(R.string.connection_problem_notification_title, connectionProblemNotification);			
	}
	
	public void notifyServiceStopped (int disableReason) {
		String strDisableReason = ctx.getString(disableReason);
		serviceStoppedNotification = new NotificationCompat.Builder(ctx)
				.setContentTitle(ctx.getString(R.string.service_closed_because_connection_problem_notification_title))
				.setContentText(ctx.getString(R.string.service_closed_because_connection_problem_notification, strDisableReason))
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_launcher)
				.setOngoing(true)
				.setContentIntent(serviceStoppedNotification.contentIntent)
				.build();
		notificationManager.notify(R.string.service_closed_because_connection_problem_notification_title, serviceStoppedNotification);		
	}
	
	public void toast (String message) {
		Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
	}
	
	public void toast (int messageId) {
		Toast.makeText(ctx, messageId, Toast.LENGTH_LONG).show();
	}
	
	
}
