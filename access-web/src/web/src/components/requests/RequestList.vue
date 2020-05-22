<template>
<v-container>
  <v-skeleton-loader v-if="REQUESTS===null" type="list-item-three-line" />
  <v-data-iterator v-if="REQUESTS!==null"
    :items="REQUESTS"
    :page="page"
    no-data-text="No requests"
    sort-by="id"
    sort-desc
    hide-default-footer
  >
    <template v-slot:header>
      
    </template>
    <template v-slot:default="props">
      <v-expansion-panels>
        <v-expansion-panel v-for="item in props.items" :key="item.id">
          <v-expansion-panel-header>
            <v-row no-gutters>
              <v-col cols="9">
                <div class="ma-2"><span class="subtitle-2">SR-{{item.id}}</span><span>: {{ item.purpose }}</span></div>
              </v-col>
              <v-col cols="3">
                <v-chip class="float-right ma-2" :color="getColor(item)" small>
                {{item.status}}: {{dateFromNow(item.lastModifiedDate)}}
              </v-chip>
              </v-col>
            </v-row>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <v-skeleton-loader type="list-item-two-line" />
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </template>

    <template v-slot:footer>
      <v-row class="mt-2" align="center" justify="center">
        <v-spacer></v-spacer>

        <span
          class="mr-4
          grey--text"
        >
          Page {{ page }} of {{ numberOfPages }}
        </span>
        <v-btn
          fab
          dark
          color="blue darken-3"
          class="mr-1"
          @click="formerPage"
        >
          <v-icon>mdi-chevron-left</v-icon>
        </v-btn>
        <v-btn
          fab
          dark
          color="blue darken-3"
          class="ml-1"
          @click="nextPage"
        >
          <v-icon>mdi-chevron-right</v-icon>
        </v-btn>
      </v-row>
    </template>
  </v-data-iterator>
</v-container>
</template>

<script>

  import {mapGetters} from 'vuex';
  import moment from 'moment';

  export default {
    data() {
      return {
        page: 1,
        itemsPerPage: 10,
      };
    },
    computed: {
      numberOfPages() {
        return Math.ceil(this.REQUESTS.length / this.itemsPerPage);
      },
      ...mapGetters(['REQUESTS']),
    },
    methods: {
      nextPage() {
        if (this.page + 1 <= this.numberOfPages) this.page += 1;
      },
      formerPage() {
        if (this.page - 1 >= 1) this.page -= 1;
      },
      dateFromNow(date) {
        return moment(date).fromNow();
      },
      getColor(request) {
        if(request.status==="DONE")
          return "success";
        else
          return "info"
      },
    },
  };
</script>

<style scoped>
  
</style>
