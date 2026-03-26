import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import './assets/main.css'

const app = createApp(App) //creates a view app to controll the webpage the result is stored in const app
app.use(createPinia())
app.use(router)
app.mount('#app') //tells the app at what point or where in the dom to mount the app
//#app is a selector and we tell it to look for elements with the id "app" and to mount the app instance there
//then the view app controlls everything inside the element exemple div with the id "app"