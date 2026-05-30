import asyncio
import aiohttp

GRAPHOPPER_API_KEY = "d3da79b1-022e-4433-82c1-00fc3baf4142"
OPENWEATHER_API_KEY = "3ea5409d5e2e1c990f9b4910a0ae96bd"
OPENTRIPMAP_API_KEY = "5ae2e3f221c38a28845f05b6d0fd9d757caff40e5080a0fff21e0299"


async def search_locations(query):
    async with aiohttp.ClientSession() as session:
        # print("starting location serch")
        url = f"https://graphhopper.com/api/1/geocode?q={query}&locale=en&limit=3&reverse=false&debug=false&point=45.93272,11.58803&provider=default&key={GRAPHOPPER_API_KEY}"
        async with session.get(url) as response:
            if response.status == 200:
                data = await response.json()
                return data.get("hits", [])  # Returns a list of location matches
            print(
                f"error occured during http get location search, http response: {response.status}"
            )
            return []


async def fetch_weather(lat, lon):
    async with aiohttp.ClientSession() as session:
        # print("starting weather search")
        url = f"https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={OPENWEATHER_API_KEY}&units=metric"
        async with session.get(url) as response:
            if response.status == 200:
                return await response.json()
            return None


async def fetch_kudago_events(city, lat, lon, radius=1000):
    async with aiohttp.ClientSession() as session:
        url = f"https://kudago.com/public-api/v1.4/events/?lon={lon}&lat={lat}&radius={radius}&fields=id,title,place,location,description,dates,participants&expand=place,location,dates,participants"
        async with session.get(url) as response:
            if response.status == 200:
                return await response.json()
            print(f"\nsearch has failed, status: {response.status}")
            return None


def location2s(location):
    return f"{location['name']} ({location['osm_value']}), {location['country']}, lattitude and logntitude: [{location['point']['lat']}, {location['point']['lng']}]"


def print_locations(locations):
    print("Found following locations: ")
    i = 1
    for location in locations:
        print(f"{i}. {location2s(location)}")
        i = i + 1


async def fetch_data(location):
    locations = await search_locations(location)
    if not locations:
        return {"error": f"No locations found for the location: {location}"}
    print_locations(locations)
    choice = 0
    if len(locations) > 1:
        choice = int(input("Choose location: "))
        choice = choice - 1
    selected_location = locations[choice]
    lat, lon = selected_location["point"]["lat"], selected_location["point"]["lng"]

    weather_task = asyncio.create_task(fetch_weather(lat, lon))
    events_task = asyncio.create_task(fetch_kudago_events(location, lat, lon))
    weather, events = await asyncio.gather(weather_task, events_task)
    if weather == None:
        return {
            "error": f"Weather was not found for location: {location2s(selected_location)}"
        }

    return {
        "location": location2s(selected_location),
        "weather": weather,
        "events": events,
    }


def weather2s(weather):
    precipitation = (
        str(weather["weather"][0]["main"])
        + ", "
        + str(weather["weather"][0]["description"])
    )
    temp = str(weather["main"]["temp"]) + ", " + str(weather["main"]["feels_like"])
    hum = str(weather["main"]["humidity"]) + "%"
    res = (
        "Precipitation: "
        + precipitation
        + "\nTemperature (in Celcius): "
        + temp
        + "\nhumidity: "
        + hum
    )
    return res


def print_event(event, i):
    if not event["dates"]:
        print("error: no dates were provided")
    else:
        dates = event["dates"][0]
        if not dates["end_date"]:
            print(
                str(i) + ".",
                event["title"],
                " - ",
                event["description"],
                "(" + str(dates["start_date"]) + ")\n",
            )
            return
        if not dates["start_time"]:
            if not dates["end_time"]:
                print(
                    str(i) + ".",
                    event["title"],
                    " - ",
                    event["description"],
                    "("
                    + str(dates["start_date"])
                    + " - "
                    + str(event["dates"][0]["end_date"])
                    + ")\n",
                )
        else:
            print(
                str(i) + ".",
                event["title"],
                " - ",
                event["description"],
                "("
                + str(dates["start_date"])
                + ", "
                + str(dates["start_time"])
                + " - "
                + str(event["dates"][0]["end_date"])
                + ", "
                + str(event["dates"][0]["end_time"])
                + ")\n",
            )


async def main():
    location = input("Enter a location: ")
    results = await fetch_data(location)

    if "error" in results:
        print(results["error"])
        return

    print("\nSelected Location:")
    print(results["location"])

    print("\nWeather:")
    print(weather2s(results["weather"]))

    print("\nFound the following events in the chosen location: ")
    i = 1
    for event in results["events"]["results"]:
        print_event(event, i)
        i = i + 1


if __name__ == "__main__":
    asyncio.run(main())
