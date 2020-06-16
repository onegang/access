<template>
  <div>
    <v-skeleton-loader v-if="MYACCESS===null" type="list-item-three-line" />
    <div v-if="MYACCESS">
      <UserDetails class="pa-4" :user="MYACCESS.user" readonly />
      <div class="pa-4">
        <h4>Requests</h4>
        <v-expansion-panels multiple>
          <v-expansion-panel v-for="item in MYACCESS.requests" :key="item.id">
            <v-expansion-panel-header>
              <v-row no-gutters>
                <v-col cols="9">
                  <div class="ma-2"><span class="subtitle-2">SR-{{item.id}}</span><span>: {{ item.purpose }}</span></div>
                </v-col>
                <v-col cols="3">
                  <v-chip class="float-right ma-2" small
                    :outlined="isPending(item)"
                    :color="getColor(item)">
                  {{item.status}}: {{dateFromNow(item.lastModifiedDate)}}
                </v-chip>
                </v-col>
              </v-row>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <RequestListItem :request="item" />
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
      </div>
      <v-divider></v-divider>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex';
import moment from 'moment';
import UserDetails from '../shared/UserDetails.vue';

export default {
  components: {
    UserDetails,
  },
  mounted() {
    this.$store.dispatch('GET_MYACCESS');
  },
  data() {
      return {
        page: 1,
        itemsPerPage: 10,
      };
    },
  computed: {
    ...mapGetters(['MYACCESS']),
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
      isPending(request) {
        return request.status==="APPROVING" || request.status==="IMPLEMENTING";
      },
      getColor(request) {
        if(request.status==="CANCELLED" || request.status==="REJECTED")
          return "deep-orange darken-3 white--text";
        else if(request.status==="DONE")
          return "info";
        else
          return "";
      },
      changeFilter() {
        this.$store.dispatch('GET_REQUESTS', this.filter);
      },
    },
};
</script>

<style scoped>
  
</style>
