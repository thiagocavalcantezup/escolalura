'use strict';

/** Helper function to generate a Google Maps directions URL */
function generateDirectionsURL(origin, destination) {
  const googleMapsUrlBase = 'https://www.google.com/maps/dir/?';
  const searchParams = new URLSearchParams('api=1');
  searchParams.append('origin', origin);
  const destinationParam = [];
  // Add title to destinationParam except in cases where Quick Builder set
  // the title to the first line of the address
  if (destination.title !== destination.address1) {
    destinationParam.push(destination.title);
  }
  destinationParam.push(destination.address1, destination.address2);
  searchParams.append('destination', destinationParam.join(','));
  return googleMapsUrlBase + searchParams.toString();
}

/**
 * Defines an instance of the Locator+ solution, to be instantiated
 * when the Maps library is loaded.
 */
function LocatorPlus(configuration) {
  const locator = this;

  locator.locations = configuration.locations || [];
  locator.capabilities = configuration.capabilities || {};

  const mapEl = document.getElementById('gmp-map');

  locator.searchLocation = null;
  locator.searchLocationMarker = null;
  locator.selectedLocationIdx = null;
  locator.userCountry = null;

  // Initialize the map -------------------------------------------------------
  locator.map = new google.maps.Map(mapEl, configuration.mapOptions);

  // Create a marker for each location.
  const markers = locator.locations.map(function (location, index) {
    const marker = new google.maps.Marker({
      position: location.coords,
      map: locator.map,
      title: location.title,
    });
    marker.addListener('click', function () {
      selectResultItem(index, false, true);
    });
    return marker;
  });

  // Fit map to marker bounds.
  locator.updateBounds = function () {
    const bounds = new google.maps.LatLngBounds();
    if (locator.searchLocationMarker) {
      bounds.extend(locator.searchLocationMarker.getPosition());
    }
    for (let i = 0; i < markers.length; i++) {
      bounds.extend(markers[i].getPosition());
    }
    locator.map.fitBounds(bounds);
  };

  if (locator.locations.length) {
    locator.updateBounds();
  }
}
