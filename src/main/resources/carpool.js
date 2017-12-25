
Vue.component('google-places-auto-complete', {
    template: `<input v-model="place">`,
    data: function () {
        return {place:'', latitude:'', longitude:''}
    },
    props:['eventBus'],
    created() {
        this.eventBus.$on('clearInput', () => {this.place = ''});
    },
    mounted: function(){
        //why do i have to attach it to the this object?
        this.autocomplete = new google.maps.places.Autocomplete(
            (this.$el),
            {types: ['geocode']});

        this.autocomplete.addListener('place_changed', () => {
            let place = this.autocomplete.getPlace();
            this.$emit('updated', place);
        })
    }
})

Vue.component('location-input',{
    template:`
<div style="display:flex; flex-flow:row wrap">
    <google-places-auto-complete v-if="automatic" :eventBus="childBus" v-on:updated="procesAutocompleteInput($event)"></google-places-auto-complete>
    <div style="display:flex; flex-flow:row wrap;" v-else>
        <div><label>latitude</label><input v-model="latitude" type="number" step="any"></div>
        <div><label>longitude</label><input v-model="longitude" type="number" step="any"></div>
    </div>
    <div>
        <button v-if="automatic" v-on:click="switchInput()">manual</button>
        <button v-else v-on:click="switchInput()">auto-complete</button>
        <button v-on:click="close()">X</button>
    </div>
</div>`,
    data: function(){
        return{
            automatic:true,
            latitude:'',
            longitude:'',
            childBus: new Vue()}
    },
    watch:{
        latitude:function(){
            //input debounce method for lat long
            this.checkManualInput()
        },
        longitude:function(){
            this.checkManualInput()
        }
    },
    methods:{
        switchInput(){this.automatic = !this.automatic},
        close: function(){this.$emit('close')},
        procesAutocompleteInput: function(place){
            if('geometry' in place){
                coordinates = {
                    latitude: place.geometry.location.lat(),
                    longitude:place.geometry.location.lng()
                }
                this.$emit('updated', coordinates);
                this.latitude = ''
                this.longitude = ''
            }
        },
        checkManualInput: _.debounce(function(){
            //if both are filled in, call update and clear input
            if(this.latitude && this.longitude){
                coordinates = {latitude: this.latitude, longitude: this.longitude}
                this.$emit('updated', coordinates);
                this.childBus.$emit("clearInput")
            }
        },500)
    }
})

function encodeQueryData(data) {
    let parameterString = [];
    for (let value in data){
        if(Array.isArray(data[value])){
            data[value].forEach(function(param){
                parameterString.push(encodeURIComponent(value) + '=' + encodeURIComponent(param));
            })
        }else{
            parameterString.push(encodeURIComponent(value) + '=' + encodeURIComponent(data[value]));
        }
    }
    return parameterString.join('&');
}

Vue.component('google-places-image', {
    template:`<div><img v-bind:src="imageSrc"></div>`,
    props: ['places'],
    computed:{
        imageSrc: function(){
            //create the map
            var queryData = {
                'size':'300x300',
                'maptype': 'roadmap',
                'key':'AIzaSyBxrrybSvnnHZfKp4EK2CmFkGQhCOZ1BxE'}
            placesToRender = this.places
            if(placesToRender != null){
                placesToRender = this.places.filter(function(e){
                    if(e.latitude && e.longitude) return true
                })
            }
            if(placesToRender == null || placesToRender.length == 0){
                queryData['zoom'] = '1'
            }else{
                let markers = []
                queryData['markers'] = placesToRender.map(function(place, index, arr){
                    label = index
                    if(label == 0) label = 'S'
                    else if(label == arr.length-1) label = 'D'
                    return 'color:green|label:'+ label +'|'+ place.latitude + ',' + place.longitude
                })
                let locations = placesToRender.map(function(place){
                    return "" + place.latitude +"," + place.longitude
                })
                queryData['path'] = "color:0x0000ff|weight:5|" + locations.join('|')
            }

            var params = encodeQueryData(queryData);
            let url = "https://maps.googleapis.com/maps/api/staticmap?" + params;
            return  url;
        }
    }
})

Vue.component('edit-stops',{
    template:`
<div style="display: flex; flex-flow: row wrap">
    <draggable style="display: flex; flex-direction: column" v-model="places" @end="notify">
        <location-input
                style="display: flex; flex-direction: row"
                v-for="place in places"
                :key="place.id"
                v-on:updated="updateLocation($event, place.id)"
                v-on:close="removePlace(place.id)"></location-input>
        <button slot="footer" v-on:click="addPlace()">Add extra stop</button>
    </draggable>
    <google-places-image :places="places"></google-places-image>
</div>
`,
    data: function(){
        return {
            places: [],
            nextId: 1
        }
    },
    methods:{
        addPlace: function(place){
            if(!place){
                place = {}
            }
            if(!place.id){
                place.id = this.nextId
                this.nextId += 1
            }
            this.places.push(place)
        },
        updateLocation: function(coordinates, id){
            rePlace = {latitude: coordinates.latitude, longitude: coordinates.longitude, id:id}
            index = this.places.findIndex(function(e){
                if(e.id === id) return true
            })
            //what if we finds no index?
            Vue.set(this.places, index, rePlace)
            this.notify()
        },
        removePlace:function(id){
            this.places = this.places.filter(function(place){
                if(place.id != id) return true
                else return false
            })
        },
        notify:function(){
            coordinateList = this.places.map(function(place){
                return{latitude: place.latitude, longitude: place.longitude}
            })
            //todo: only update when all places are valid coordinates.
            this.$emit("updated", coordinateList)
        }
    },
    mounted: function() {
        this.addPlace({})
        this.addPlace({})
    }
})

Vue.component("new-trip",{
    template:`
<div style="display: flex; flex-direction: column">
    <div style="display: flex; flex-flow:row wrap">
        <div><label>name</label> <input v-model="info.name"></div>
        <div><label>email</label> <input v-model="info.email"></div>
        <div><label>max. passengers</label> <input style="width: 2em; " v-model="info.max_passengers" type="number"></div>
        <div><label>departure</label> <input class="departure" type="datetime-local"></div>
    </div>  
    <edit-stops @updated="update"></edit-stops>
    <button @click="postTrips">post trip</button>
</div>
    `,
    data:function(){
        return{
            info:{},
            stops:[]
        }
    },
    methods:{
        postTrips(){
            console.log("posting trip to server :-)", this.stops)
            //verify input
        },
        update(stops){
            console.log("updating stops", stops)
            this.stops = stops;
        }
    }
})

Vue.component("trip",{
    template:`
<div style="display: flex; flex-direction: column">
    <div style="display: flex; flex-flow:row wrap">
        <div><label>name</label> <span>{{trip.info.name}}</span></div>
        <div><label>email</label> <span>{{trip.info.email}}</span></div>
        <div><label>max. passengers</label> <span>{{trip.info.max_passengers}}</span></div>
        <div><label>departure</label> <span>{{trip.info.departure}}</span></div>
    </div>  
    <div style="display: flex; flex-direction: row">
        <google-places-image :places="trip.stops"></google-places-image>
        <div v-for="stop in trip.stops" style="display: flex; flex-direction: column">
            {{stop.latitude}} {{stop.longitude}}
        </div>
    </div>
</div>`,
    props:["trip"]
})

Vue.component("trip-list", {
    template: `<div><trip v-for="trip in trips" style="display: flex; flex-direction: column" :trip="trip"/></div>`,
    data:function(){
        return {
            trips:[
                {
                    info:{name:"test1", email:"e@mail.com", max_passengers:3, departure:"2017-3-1"},
                    stops:[
                        {latitude:1, longitude:2},
                        {latitude:3, longitude:4}
                ]},
                {
                    info:{name:"test2", email:"e@mail.com", max_passengers:3, departure:"2017-3-1"},
                    stops:[
                        {latitude:1, longitude:2},
                        {latitude:3, longitude:4}
                ]}
            ]
        }
    },
    mounted: function () {
        console.log("triplist mounted")
    }
})

var app = new Vue({
    el: '#app'
})