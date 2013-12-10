/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//      Created By :            Vegard Engen
//      Created Date :          2013-10-28
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.sim.ws.impl;

/**
 * 
 * @author Vegard Engen
 */
public class ForumStats
{
	private int posts;
	private int newThreads;
	private int answeredThreads;
	private int unansweredThreads;
	private double meanTRT; // mean thread resolution time in hours
	
	public ForumStats() 
	{
		posts = 0;
		newThreads = 0;
		answeredThreads = 0;
		meanTRT = 0;
	}
	
	public ForumStats(int posts, int newThreads, int answeredThreads, double meanTRT)
	{
		this.posts = posts;
		this.newThreads = newThreads;
		this.answeredThreads = answeredThreads;
		this.meanTRT = meanTRT;
	}

	/**
	 * @return the posts
	 */
	public int getPosts()
	{
		return posts;
	}

	/**
	 * @param posts the posts to set
	 */
	public void setPosts(int posts)
	{
		this.posts = posts;
	}

	/**
	 * @return the newThreads
	 */
	public int getNewThreads()
	{
		return newThreads;
	}

	/**
	 * @param newThreads the newThreads to set
	 */
	public void setNewThreads(int newThreads)
	{
		this.newThreads = newThreads;
	}

	/**
	 * @return the answeredThreads
	 */
	public int getAnsweredThreads()
	{
		return answeredThreads;
	}

	/**
	 * @param answeredThreads the answeredThreads to set
	 */
	public void setAnsweredThreads(int answeredThreads)
	{
		this.answeredThreads = answeredThreads;
	}

	/**
	 * @return the meanTRT
	 */
	public double getMeanTRT()
	{
		return meanTRT;
	}

	/**
	 * @param meanTRT the meanTRT to set
	 */
	public void setMeanTRT(double meanTRT)
	{
		this.meanTRT = meanTRT;
	}

	/**
	 * @return the unansweredThreads
	 */
	public int getUnansweredThreads()
	{
		return unansweredThreads;
	}

	/**
	 * @param unansweredThreads the unansweredThreads to set
	 */
	public void setUnansweredThreads(int unansweredThreads)
	{
		this.unansweredThreads = unansweredThreads;
	}
}
