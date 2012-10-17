package org.benjp.services;

import com.mongodb.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.net.UnknownHostException;
import java.util.*;

@Named("userService")
@ApplicationScoped
public class UserService
{

  private static Mongo m;

  private static final String M_DB = "users";
  private static final String M_SESSIONS_COLLECTION = "sessions";

  public UserService() throws UnknownHostException
  {
    m = new Mongo("localhost");
    m.setWriteConcern(WriteConcern.SAFE);
  }

  private DB db()
  {
    return m.getDB(M_DB);
  }


  public boolean hasSession(String session)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("session", session);
    DBCursor cursor = coll.find(query);
    return (cursor.hasNext());
  }

  public boolean hasUser(String user)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    return (cursor.hasNext());
  }

  public boolean hasUserWithSession(String user, String session)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    query.put("session", session);
    DBCursor cursor = coll.find(query);
    return (cursor.hasNext());
  }

  public void addUser(String user, String session)
  {
    if (!hasUserWithSession(user, session))
    {
      System.out.println("USER SERVICE :: ADDING :: " + user + " : " + session);
      removeUser(user);
      DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);

      BasicDBObject doc = new BasicDBObject();
      doc.put("user", user);
      doc.put("session", session);

      coll.insert(doc);
    }
  }

  public void removeSession(String session)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("session", session);
    DBCursor cursor = coll.find(query);
    if (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      String user = doc.get("user").toString();
      System.out.println("USER SERVICE :: REMOVING :: " + user + " : " + session);
      coll.remove(doc);
    }
  }

  private void removeUser(String user)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    while (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      coll.remove(doc);
    }
  }

  public List<String> getUsers()
  {
    ArrayList<String> users = new ArrayList<String>();
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    DBCursor cursor = coll.find();
    while (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      users.add(doc.get("user").toString());
    }

    return users;
  }

  public List<String> getUsersFilterBy(String user)
  {
    ArrayList<String> users = new ArrayList<String>();
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    DBCursor cursor = coll.find();
    while (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      String target = doc.get("user").toString();
      if (!user.equals(target))
        users.add(target);
    }

    return users;
  }

}