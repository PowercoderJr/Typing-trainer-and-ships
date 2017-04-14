package typingtrainer;

import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Meow on 14.04.2017.
 */
public class ServerInfo implements Serializable
{
	private static final long serialVersionUID = 88005553535L;

	private SimpleStringProperty name;
	private SimpleStringProperty ip;
	private SimpleStringProperty passwordFlag;

	public ServerInfo(String name, String ip, String passwordFlag)
	{
		this.name = new SimpleStringProperty(name);
		this.ip = new SimpleStringProperty(ip);
		this.passwordFlag = new SimpleStringProperty(passwordFlag);
	}

	private void writeObject(ObjectOutputStream stream) throws IOException
	{
		stream.writeObject(name.get());
		stream.writeObject(ip.get());
		stream.writeObject(passwordFlag.get());
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		name = new SimpleStringProperty((String) stream.readObject());
		ip = new SimpleStringProperty((String) stream.readObject());
		passwordFlag = new SimpleStringProperty((String) stream.readObject());
	}

	public String getName()
	{
		return name.get();
	}

	public SimpleStringProperty nameProperty()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name.set(name);
	}

	public String getIp()
	{
		return ip.get();
	}

	public SimpleStringProperty ipProperty()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip.set(ip);
	}

	public String getPasswordFlag()
	{
		return passwordFlag.get();
	}

	public SimpleStringProperty passwordFlagProperty()
	{
		return passwordFlag;
	}

	public void setPasswordFlag(String passwordFlag)
	{
		this.passwordFlag.set(passwordFlag);
	}
}