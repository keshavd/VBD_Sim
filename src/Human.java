import java.util.Calendar;


/*
 *	This file is part of DiseaseSim version 0.3 -  an agent based modeling research tool	*
 *	Copyright (C) 2012 Marek Laskowski				*
 *											*
 *	This program is free software: you can redistribute it and/or modify		*
 *	it under the terms of the GNU General Public License as published by		*
 *	the Free Software Foundation, either version 3 of the License, or		*
 *	(at your option) any later version.						*
 *											*
 *	This program is distributed in the hope that it will be useful,			*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of			*
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			*
 *	GNU General Public License for more details.					*
 *											*
 *	You should have received a copy of the GNU General Public License		*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.		*
 *											*
 *	email: mareklaskowski@gmail.com							*
 ****************************************************************************************/
/**
 * 
 * a class reprsenting a human agent
 * maintains the agent's home row and column to which the agent returns
 * also stores the infectious period for a human
 */
public class Human extends Agent {
	int homeRow;
	int homeCol;
	String gender;
	
	
	public static final String type = "HUMAN";
	public static final double infectious_period =604800; //7*3600*24 = 604800
	
	/**
	 * a constructor that sets this agent's home row and column
	 * also this will be the agent's initial location
	 * @param homeRow the human's home row
	 * @param homeCol the human's home column
	 */
	public Human(int homeRow, int homeCol){
		super(type, homeRow, homeCol);
		this.homeRow = homeRow;
		this.homeCol = homeCol;
		this.gender = "NA";
	}
	public Human(int homeRow, int homeCol, String gender){
		super(type, homeRow, homeCol);
		this.homeRow = homeRow;
		this.homeCol = homeCol;
		this.gender = gender;
		
	}

	/**
	 * get bitten by a mosquito
	 * @param biter a reference to the biting mosquito
	 */
	public void recieveBite(Mosquito biter)
	{
		if(biter.isInfected())
		{
			recieveDisease(biter.getStrain());
		}
		
		if(isInfected()){
			biter.recieveDisease(getStrain());
		}
	}
	
	/**
	 * simulate the agent's behavior for this time period
	 * @param deltaTime time that the simulation has advanced
	 */
	@Override
	public void tick(double deltaTime){
		super.tick(deltaTime);
		//check to see if recovered yet
		for(Disease disease : infections)
		{
			if(disease.getState() == Disease.State.INFECTED && disease.getTimeSinceInfection() > infectious_period)
			{
				disease.setState(Disease.State.RECOVERED);
				System.out.println("agent" + getUID() + " " + getType() + " recovered");

			}
		}
		
		//basic behavior: half the time levy flight, half the time return home
		//at night stay home
		if(World.getTime().get(Calendar.HOUR_OF_DAY) < 8)
		{
				//in a different spot so go home
				moveTo(homeRow, homeCol);
			
		}else{
			//half the time return home
			if(Math.random() < 0.5)
			{
				moveTo(homeRow, homeCol);
			}else{
				//half the time levy fight
				moveTo(levyFlight(getRow()),levyFlight(getColumn()));
			}
		}
		
	}
	
}
