import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Calendar;
import java.util.Random;

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
 * a class representing the simulated world, contains the lattice environment
 * maintains the time between simulation frames, and the current time within the model
 *
 */
public class World {
	private static Environment[][] lattice;
	double timeStepSeconds;
	private static Calendar currTime = Calendar.getInstance();
	
	/**
	 * 
	 * @return the current model time
	 */
	public static Calendar getTime()
	{
		return currTime;
	}
	
	/**
	 * I thought there was a math function to compute factorials. oh well. recursion, hooray
	 * @param k this function will compute the factorial of k, as in k!
	 * @return k! as long int
	 */
	private static long factorial(long k)
	{
		if(k < 1) return 1;
		else
		{
			return factorial(k-1) * k;
		}
	}
	
	
	
	/**
	 * computes the poisson probability
	 * @param rate the poisson rate
	 * @param k the number of events
	 * @return the probability of k events given the poisson rate
	 */
	public static double poissonProbability(double rate, int k)
	{
		double prob = 0;
		prob = (Math.exp(-rate)* Math.pow(rate,k)) / factorial(k);
		return prob;
		
	}
	/*
	 * for sampling how many statistical events happen within one time period given some average rate
	 * see http://en.wikipedia.org/wiki/Poisson_process
	 * @param rate is the poisson rate
	 * returns the number of events
	 * 
	 */
	/**
	 * for sampling how many statistical events happen within one time period given some average rate
	 * see http://en.wikipedia.org/wiki/Poisson_process
	 * @param rate the poisson rate
	 * @return the number of events sampled given the current rate
	 */
	public int samplePoisson(double rate)
	{
		double sample =  Math.random();
		int k = 0;
		while(sample < poissonProbability(rate,k))
		{
			k++;
		}
		return k;
	}
	
	/**
	 * a constructor for the world
	 * @param rows how many rows the world has
	 * @param cols how many columns the world has
	 * @param timeStepSeconds the time between simulation steps
	 * @param averageMosquitoDensity a number representing the mosquito density in the environment
	 * @param averageHumanDensity a number representing the human density in the environment
	 */
	public World(int rows, int cols, double timeStepSeconds, double averageMosquitoDensity, double averageHumanDensity, double genderRatio){
		this.timeStepSeconds = timeStepSeconds;
		
		lattice = new Environment[rows][cols];
		
		for(int i = 0; i< rows; i++){
			for(int j = 0; j < cols; j++){
				lattice[i][j] = new Environment(i,j,averageMosquitoDensity);
				for(int numHumans = samplePoisson(averageHumanDensity); numHumans > 0; numHumans--)
				{
					if (new Random().nextDouble()<=genderRatio){
						Human resident = new Human(i,j,"male");
						lattice[i][j].enter(resident);
					}else{
						Human resident = new Human(i,j,"female");
						lattice[i][j].enter(resident);
					}
					
				}
			}
		}
	}
	
	/*
	 * @return an environment at row, col in the lattice. may return null if called before a world is constructed
	 */
	/**
	 * get the environment in the specified location
	 * @param row the environment's row
	 * @param col the environment's column
	 * @return the environment at row, col in the lattice. may return null if called before a world is constructed
	 */
	public static Environment getLocation(int row, int col)
	{
		
		if(row < 0){
			row = 0;
		}
		if(col < 0)
		{
			col = 0;
		}
		if(row >= lattice.length)
		{
			row = lattice.length - 1;
		}
		if (col >= lattice[0].length)
		{
			col = lattice[0].length - 1;
		}
		return lattice[row][col];
	}
	
	/**
	 * get a random lattice location
	 * @return the randomly chosen environment
	 */
	public static Environment getRandomLocation()
	{
		int randRow = (int) (lattice.length * Math.random());
		int randCol = (int) (lattice[randRow].length * Math.random());
		return lattice[randRow][randCol];
	}
	/**
	 * advance the simulation	 
	 */
	public void tick()
	{
		for(int i = 0; i< lattice.length; i++){
			for(int j = 0; j < lattice[i].length; j++){
				lattice[i][j].tick(timeStepSeconds);
			}
		}
		for(int i = 0; i< lattice.length; i++){
			for(int j = 0; j < lattice[i].length; j++){
				//empty exitant lists
				lattice[i][j].doExits();
				//empty entrant lists
				lattice[i][j].doEntrances();
			}
		}
		//update calendar time
		currTime.add(Calendar.SECOND, (int)timeStepSeconds);
	}
	/**
	 * count the number of infected agents in the simulation
	 * @return the number of infected agents
	 */
	public int countInfections()
	{
		int count =0;
		for(int i = 0; i< lattice.length; i++){
			for(int j = 0; j < lattice[i].length; j++){
				count += lattice[i][j].countInfections();			}
		}
		return count;
	}
	
	/**
	 * get the number of rows in the environment lattice
	 * @return the number of rows in the environment lattice
	 */
	public static int getRows()
	{
		return lattice.length;
	}
	/**
	 * get the number of columns in the environment lattice
	 * @return the number of columms in the environment lattice
	 */
	public static int getColumns()
	{
		return lattice[0].length;
	}
	/**
	 * draw this lattice location within the given render area on the provided graphics context
	 * @param g the graphics context
	 * @param renderArea a rectangle within which to draw this location
	 */
	public void render(Graphics g, Rectangle renderArea)
	{
		g.setColor(Color.white);
		g.fillRect(renderArea.x,renderArea.y,renderArea.width,renderArea.height);
		g.setColor(Color.black);
		//now divide up render area into equal parts for each Environment location
		int locationWidth = renderArea.width / getColumns();
		int locationHeight = renderArea.height / getRows();
		for(int row = 0; row< lattice.length; row++){
			for(int column = 0; column < lattice[row].length; column++){
				Rectangle locationRender = new Rectangle(column*locationWidth, row*locationHeight, locationWidth, locationHeight);
				lattice[row][column].render(g, locationRender);
			}
		}
	}
	
}
