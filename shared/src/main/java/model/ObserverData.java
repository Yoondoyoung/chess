package model;

import java.util.Collection;

public record ObserverData (GameData gameData, Collection<String> observers) {}